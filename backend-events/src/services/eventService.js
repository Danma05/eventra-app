const {
  getAllEvents,
  getEventById,
  createEvent,
  updateEvent,
  deleteEvent,
} = require("../models/eventModel");

const listEvents = async () => {
  return await getAllEvents();
};

const findEventById = async (id) => {
  const event = await getEventById(id);

  if (!event) {
    const error = new Error("Evento no encontrado");
    error.statusCode = 404;
    throw error;
  }

  return event;
};

const registerEvent = async (authUserId, data) => {
  const { title, event_date, location, capacity } = data;

  if (!title || !event_date || !location || capacity === undefined) {
    const error = new Error("title, event_date, location y capacity son obligatorios");
    error.statusCode = 400;
    throw error;
  }

  const newEvent = await createEvent({
    organizer_auth_user_id: authUserId,
    title: data.title,
    description: data.description || null,
    event_date: data.event_date,
    location: data.location,
    capacity: data.capacity,
  });

  return newEvent;
};

const editEvent = async (id, data, authUserId) => {
  const existingEvent = await getEventById(id);

  if (!existingEvent) {
    const error = new Error("Evento no encontrado");
    error.statusCode = 404;
    throw error;
  }

  if (Number(existingEvent.organizer_auth_user_id) !== Number(authUserId)) {
    const error = new Error("No tienes permiso para modificar este evento");
    error.statusCode = 403;
    throw error;
  }

  const updatedEvent = await updateEvent(id, {
    title: data.title || existingEvent.title,
    description: data.description ?? existingEvent.description,
    event_date: data.event_date || existingEvent.event_date,
    location: data.location || existingEvent.location,
    capacity: data.capacity ?? existingEvent.capacity,
    status: data.status || existingEvent.status,
  });

  return updatedEvent;
};

const removeEvent = async (id, authUserId) => {
  const existingEvent = await getEventById(id);

  if (!existingEvent) {
    const error = new Error("Evento no encontrado");
    error.statusCode = 404;
    throw error;
  }

  if (Number(existingEvent.organizer_auth_user_id) !== Number(authUserId)) {
    const error = new Error("No tienes permiso para eliminar este evento");
    error.statusCode = 403;
    throw error;
  }

  const deletedEvent = await deleteEvent(id);

  if (!deletedEvent) {
    const error = new Error("Evento no encontrado");
    error.statusCode = 404;
    throw error;
  }

  return deletedEvent;
};

module.exports = {
  listEvents,
  findEventById,
  registerEvent,
  editEvent,
  removeEvent,
};