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
    throw new Error("Evento no encontrado");
  }

  return event;
};

const registerEvent = async (authUserId, data) => {
  const { title, event_date, location, capacity } = data;

  if (!title || !event_date || !location || !capacity) {
    throw new Error("title, event_date, location y capacity son obligatorios");
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

const editEvent = async (id, data) => {
  const existingEvent = await getEventById(id);

  if (!existingEvent) {
    throw new Error("Evento no encontrado");
  }

  const updatedEvent = await updateEvent(id, {
    title: data.title || existingEvent.title,
    description: data.description ?? existingEvent.description,
    event_date: data.event_date || existingEvent.event_date,
    location: data.location || existingEvent.location,
    capacity: data.capacity || existingEvent.capacity,
    status: data.status || existingEvent.status,
  });

  return updatedEvent;
};

const removeEvent = async (id) => {
  const deletedEvent = await deleteEvent(id);

  if (!deletedEvent) {
    throw new Error("Evento no encontrado");
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