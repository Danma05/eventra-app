const { checkUserRegistration } = require("../clients/registrationClient");
const { getEventById } = require("../clients/eventClient");
const {
  findOpenSessionByUser,
  createActivitySession,
  findSessionById,
  saveActivityLocation,
  updateLiveSessionStats,
  finishActivitySession,
  pauseActivitySession,
  resumeActivitySession,
  markActiveSessionsAsDnfByEvent,
  getLiveRankingByEvent,
} = require("../models/activityModel");

const FINISH_RADIUS_METERS = Number(process.env.FINISH_RADIUS_METERS || 20);

const haversineMeters = (lat1, lon1, lat2, lon2) => {
  const R = 6371000;
  const toRad = (value) => (Number(value) * Math.PI) / 180;
  const dLat = toRad(lat2 - lat1);
  const dLon = toRad(lon2 - lon1);
  const a =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
    Math.sin(dLon / 2) * Math.sin(dLon / 2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return R * c;
};

const assertRaceCanTrack = async (eventId) => {
  const event = await getEventById(eventId);

  if (!event) {
    const error = new Error("Evento no encontrado");
    error.statusCode = 404;
    throw error;
  }

  if (!event.finish_latitude || !event.finish_longitude) {
    const error = new Error("El organizador aún no definió la meta de la carrera");
    error.statusCode = 400;
    throw error;
  }

  if (event.race_status === "PAUSED") {
    const error = new Error("La carrera está pausada por el organizador");
    error.statusCode = 409;
    throw error;
  }

  if (event.race_status !== "STARTED") {
    const error = new Error("La carrera todavía no ha sido iniciada por el organizador");
    error.statusCode = 409;
    throw error;
  }

  return event;
};

const startActivity = async (eventId, authUserId, token) => {
  if (!eventId) {
    const error = new Error("event_id es obligatorio");
    error.statusCode = 400;
    throw error;
  }

  await assertRaceCanTrack(eventId);

  const registration = await checkUserRegistration(eventId, token);
  if (!registration.registered) {
    const error = new Error("No estás inscrito en este evento");
    error.statusCode = 403;
    throw error;
  }

  const openSession = await findOpenSessionByUser(authUserId);
  if (openSession) {
    const error = new Error("Ya tienes una actividad activa o pausada");
    error.statusCode = 409;
    throw error;
  }

  return createActivitySession(eventId, authUserId);
};

const registerLocation = async (data, authUserId) => {
  const {
    activity_session_id,
    latitude,
    longitude,
    altitude,
    speed_kmh,
    accuracy_meters,
    total_time_seconds,
    total_distance_km,
    average_speed_kmh,
  } = data;

  if (!activity_session_id || latitude === undefined || longitude === undefined) {
    const error = new Error("activity_session_id, latitude y longitude son obligatorios");
    error.statusCode = 400;
    throw error;
  }

  const session = await findSessionById(activity_session_id);
  if (!session) {
    const error = new Error("Sesión de actividad no encontrada");
    error.statusCode = 404;
    throw error;
  }

  if (Number(session.auth_user_id) !== Number(authUserId)) {
    const error = new Error("No tienes permiso para registrar ubicación en esta actividad");
    error.statusCode = 403;
    throw error;
  }

  if (session.activity_status !== "ACTIVE") {
    const error = new Error("La actividad no está activa");
    error.statusCode = 400;
    throw error;
  }

  const event = await assertRaceCanTrack(session.event_id);

  const distanceToFinishMeters = haversineMeters(
    Number(latitude),
    Number(longitude),
    Number(event.finish_latitude),
    Number(event.finish_longitude)
  );

  const location = await saveActivityLocation({
    activity_session_id,
    latitude,
    longitude,
    altitude,
    speed_kmh,
    accuracy_meters,
  });

  await updateLiveSessionStats({
    sessionId: activity_session_id,
    latitude,
    longitude,
    distanceToFinishMeters,
    total_time_seconds,
    total_distance_km,
    average_speed_kmh,
  });

  let autoFinished = false;
  let finishedSession = null;

  if (distanceToFinishMeters <= FINISH_RADIUS_METERS) {
    autoFinished = true;
    finishedSession = await finishActivitySession({
      sessionId: activity_session_id,
      total_time_seconds: total_time_seconds ?? session.total_time_seconds,
      total_distance_km: total_distance_km ?? session.total_distance_km,
      average_speed_kmh: average_speed_kmh ?? session.average_speed_kmh,
      average_pace_seconds_per_km: null,
      calories_burned: 0,
    });
  }

  return {
    location,
    distance_to_finish_meters: Math.round(distanceToFinishMeters * 100) / 100,
    auto_finished: autoFinished,
    session: finishedSession,
  };
};

const finishActivity = async (data, authUserId) => {
  const {
    activity_session_id,
    total_time_seconds,
    total_distance_km,
    average_speed_kmh,
    average_pace_seconds_per_km,
    calories_burned,
  } = data;

  if (!activity_session_id) {
    const error = new Error("activity_session_id es obligatorio");
    error.statusCode = 400;
    throw error;
  }

  const session = await findSessionById(activity_session_id);
  if (!session) {
    const error = new Error("Sesión de actividad no encontrada");
    error.statusCode = 404;
    throw error;
  }

  if (Number(session.auth_user_id) !== Number(authUserId)) {
    const error = new Error("No tienes permiso para finalizar esta actividad");
    error.statusCode = 403;
    throw error;
  }

  if (!["ACTIVE", "PAUSED"].includes(session.activity_status)) {
    const error = new Error("La actividad ya no está abierta");
    error.statusCode = 400;
    throw error;
  }

  return finishActivitySession({
    sessionId: activity_session_id,
    total_time_seconds: total_time_seconds || 0,
    total_distance_km: total_distance_km || 0,
    average_speed_kmh: average_speed_kmh || 0,
    average_pace_seconds_per_km,
    calories_burned: calories_burned || 0,
  });
};

const pauseActivity = async (sessionId, authUserId) => {
  const session = await findSessionById(sessionId);
  if (!session) {
    const error = new Error("Sesión de actividad no encontrada");
    error.statusCode = 404;
    throw error;
  }
  if (Number(session.auth_user_id) !== Number(authUserId)) {
    const error = new Error("No tienes permiso para pausar esta actividad");
    error.statusCode = 403;
    throw error;
  }
  const paused = await pauseActivitySession(sessionId);
  if (!paused) {
    const error = new Error("La actividad no está activa");
    error.statusCode = 409;
    throw error;
  }
  return paused;
};

const resumeActivity = async (sessionId, authUserId) => {
  const session = await findSessionById(sessionId);
  if (!session) {
    const error = new Error("Sesión de actividad no encontrada");
    error.statusCode = 404;
    throw error;
  }
  if (Number(session.auth_user_id) !== Number(authUserId)) {
    const error = new Error("No tienes permiso para reanudar esta actividad");
    error.statusCode = 403;
    throw error;
  }
  await assertRaceCanTrack(session.event_id);
  const resumed = await resumeActivitySession(sessionId);
  if (!resumed) {
    const error = new Error("La actividad no está pausada");
    error.statusCode = 409;
    throw error;
  }
  return resumed;
};

const finishOpenSessionsByEvent = async (eventId) => {
  return markActiveSessionsAsDnfByEvent(eventId);
};

const listLiveRanking = async (eventId) => {
  if (!eventId) {
    const error = new Error("eventId es obligatorio");
    error.statusCode = 400;
    throw error;
  }

  const participants = await getLiveRankingByEvent(eventId);
  return participants.map((p) => ({
    activity_session_id: p.activity_session_id,
    event_id: p.event_id,
    auth_user_id: p.auth_user_id,
    started_at: p.started_at,
    finished_at: p.finished_at,
    total_time_seconds: Number(p.total_time_seconds || 0),
    total_distance_km: Number(p.total_distance_km || 0),
    average_speed_kmh: Number(p.average_speed_kmh || 0),
    latitude: p.latitude === null ? null : Number(p.latitude),
    longitude: p.longitude === null ? null : Number(p.longitude),
    distance_to_finish_meters: p.distance_to_finish_meters === null ? null : Number(p.distance_to_finish_meters),
    gap_to_previous_meters: p.gap_to_previous_meters === null ? null : Number(p.gap_to_previous_meters),
    current_position: Number(p.current_position),
    status: p.activity_status,
    is_tracking: p.latitude !== null && p.longitude !== null,
  }));
};

module.exports = {
  startActivity,
  registerLocation,
  finishActivity,
  pauseActivity,
  resumeActivity,
  finishOpenSessionsByEvent,
  listLiveRanking,
  listActiveParticipants: listLiveRanking,
};
