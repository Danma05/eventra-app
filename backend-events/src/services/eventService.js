const {
  getAllEvents,
  getEventById,
  createEvent,
  updateEvent,
  deleteEvent,
  getEventsByOrganizer,
  updateEventRoute,
  updateRaceStatus,
} = require("../models/eventModel");

const listEvents = async () => getAllEvents();

const findEventById = async (id) => {
  const event = await getEventById(id);
  if (!event) {
    const error = new Error("Evento no encontrado");
    error.statusCode = 404;
    throw error;
  }
  return event;
};

const assertOrganizer = (event, authUserId) => {
  if (Number(event.organizer_auth_user_id) !== Number(authUserId)) {
    const error = new Error("No tienes permiso para administrar este evento");
    error.statusCode = 403;
    throw error;
  }
};

const validateCoordinate = (value, min, max, field) => {
  const number = Number(value);
  if (Number.isNaN(number) || number < min || number > max) {
    const error = new Error(`${field} debe ser un número entre ${min} y ${max}`);
    error.statusCode = 400;
    throw error;
  }
  return number;
};

const registerEvent = async (authUserId, data) => {
  const { title, event_date, location, capacity } = data;

  if (!title || !event_date || !location || capacity === undefined) {
    const error = new Error("title, event_date, location y capacity son obligatorios");
    error.statusCode = 400;
    throw error;
  }

  return createEvent({
    organizer_auth_user_id: authUserId,
    title: data.title,
    description: data.description || null,
    event_date: data.event_date,
    location: data.location,
    capacity: data.capacity,
    image_url: data.image_url || null,
    start_latitude: data.start_latitude,
    start_longitude: data.start_longitude,
    finish_latitude: data.finish_latitude,
    finish_longitude: data.finish_longitude,
  });
};

const editEvent = async (id, data, authUserId) => {
  const existingEvent = await findEventById(id);
  assertOrganizer(existingEvent, authUserId);

  return updateEvent(id, {
    title: data.title || existingEvent.title,
    description: data.description ?? existingEvent.description,
    event_date: data.event_date || existingEvent.event_date,
    location: data.location || existingEvent.location,
    capacity: data.capacity ?? existingEvent.capacity,
    status: data.status || existingEvent.status,
    image_url: data.image_url ?? existingEvent.image_url,
  });
};

const removeEvent = async (id, authUserId) => {
  const existingEvent = await findEventById(id);
  assertOrganizer(existingEvent, authUserId);
  return deleteEvent(id);
};

const listEventsByOrganizer = async (authUserId) => getEventsByOrganizer(authUserId);

const defineRoute = async (id, data, authUserId) => {
  const event = await findEventById(id);
  assertOrganizer(event, authUserId);

  if (["STARTED", "PAUSED", "FINISHED"].includes(event.race_status)) {
    const error = new Error("No puedes modificar la ruta de una carrera iniciada o finalizada");
    error.statusCode = 409;
    throw error;
  }

  const start_latitude = validateCoordinate(data.start_latitude, -90, 90, "start_latitude");
  const start_longitude = validateCoordinate(data.start_longitude, -180, 180, "start_longitude");
  const finish_latitude = validateCoordinate(data.finish_latitude, -90, 90, "finish_latitude");
  const finish_longitude = validateCoordinate(data.finish_longitude, -180, 180, "finish_longitude");

  return updateEventRoute({ id, start_latitude, start_longitude, finish_latitude, finish_longitude });
};

const startRace = async (id, authUserId) => {
  const event = await findEventById(id);
  assertOrganizer(event, authUserId);

  if (!event.start_latitude || !event.start_longitude || !event.finish_latitude || !event.finish_longitude) {
    const error = new Error("Primero debes definir punto de salida y punto de meta");
    error.statusCode = 400;
    throw error;
  }

  if (!["READY", "CREATED"].includes(event.race_status)) {
    const error = new Error(`No puedes iniciar una carrera en estado ${event.race_status}`);
    error.statusCode = 409;
    throw error;
  }

  return updateRaceStatus(id, "STARTED");
};

const pauseRace = async (id, authUserId) => {
  const event = await findEventById(id);
  assertOrganizer(event, authUserId);

  if (event.race_status !== "STARTED") {
    const error = new Error("Solo puedes pausar una carrera iniciada");
    error.statusCode = 409;
    throw error;
  }

  return updateRaceStatus(id, "PAUSED");
};

const resumeRace = async (id, authUserId) => {
  const event = await findEventById(id);
  assertOrganizer(event, authUserId);

  if (event.race_status !== "PAUSED") {
    const error = new Error("Solo puedes reanudar una carrera pausada");
    error.statusCode = 409;
    throw error;
  }

  return updateRaceStatus(id, "STARTED");
};

const finishRace = async (id, authUserId) => {
  const event = await findEventById(id);
  assertOrganizer(event, authUserId);

  if (["FINISHED", "CANCELLED"].includes(event.race_status)) {
    const error = new Error("La carrera ya está cerrada");
    error.statusCode = 409;
    throw error;
  }

  return updateRaceStatus(id, "FINISHED");
};

module.exports = {
  listEvents,
  findEventById,
  registerEvent,
  editEvent,
  removeEvent,
  listEventsByOrganizer,
  defineRoute,
  startRace,
  pauseRace,
  resumeRace,
  finishRace,
};
