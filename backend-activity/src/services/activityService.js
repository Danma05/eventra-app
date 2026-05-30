const { checkUserRegistration } = require("../clients/registrationClient");

const {
  findActiveSessionByUser,
  createActivitySession,
  findSessionById,
  saveActivityLocation,
  finishActivitySession,
  getActiveParticipantsByEvent,
} = require("../models/activityModel");

const startActivity = async (eventId, authUserId, token) => {
  if (!eventId) {
    const error = new Error("event_id es obligatorio");
    error.statusCode = 400;
    throw error;
  }

  const registration = await checkUserRegistration(eventId, token);

  if (!registration.registered) {
    const error = new Error("No estás inscrito en este evento");
    error.statusCode = 403;
    throw error;
  }

  const activeSession = await findActiveSessionByUser(authUserId);

  if (activeSession) {
    const error = new Error("Ya tienes una actividad activa");
    error.statusCode = 409;
    throw error;
  }

  return await createActivitySession(eventId, authUserId);
};

const registerLocation = async (data, authUserId) => {
  const {
    activity_session_id,
    latitude,
    longitude,
    altitude,
    speed_kmh,
    accuracy_meters,
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

  return await saveActivityLocation({
    activity_session_id,
    latitude,
    longitude,
    altitude,
    speed_kmh,
    accuracy_meters,
  });
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

  if (session.activity_status !== "ACTIVE") {
    const error = new Error("La actividad ya no está activa");
    error.statusCode = 400;
    throw error;
  }

  const finished = await finishActivitySession({
    sessionId: activity_session_id,
    total_time_seconds: total_time_seconds || 0,
    total_distance_km: total_distance_km || 0,
    average_speed_kmh: average_speed_kmh || 0,
    average_pace_seconds_per_km,
    calories_burned,
  });

  return finished;
};

const listActiveParticipants = async (eventId) => {
  if (!eventId) {
    const error = new Error("eventId es obligatorio");
    error.statusCode = 400;
    throw error;
  }

  const participants = await getActiveParticipantsByEvent(eventId);

    return participants.map((participant) => ({
    activity_session_id: participant.activity_session_id,
    event_id: participant.event_id,
    auth_user_id: participant.auth_user_id,
    started_at: participant.started_at,
    total_time_seconds: participant.total_time_seconds,
    total_distance_km: participant.total_distance_km,
    average_speed_kmh: participant.average_speed_kmh,
    latitude: participant.latitude,
    longitude: participant.longitude,
    altitude: participant.altitude,
    speed_kmh: participant.speed_kmh,
    accuracy_meters: participant.accuracy_meters,
    recorded_at: participant.recorded_at,
    current_position: Number(participant.current_position),
    is_tracking: participant.latitude !== null && participant.longitude !== null,
  }));
};

module.exports = {
  startActivity,
  registerLocation,
  finishActivity,
  listActiveParticipants,
};