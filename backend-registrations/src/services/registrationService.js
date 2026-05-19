const axios = require("axios");

const {
  findRegistrationByEventAndUser,
  createRegistration,
  getMyRegistrations,
  countRegistrationsByEvents,
  getParticipantsByEventId,
} = require("../models/registrationModel");

const pool = require("../config/db");

const registerToEvent = async (eventId, authUserId) => {

  if (!eventId) {

    const error = new Error("event_id es obligatorio");
    error.statusCode = 400;
    throw error;

  }

  const existingRegistration =
    await findRegistrationByEventAndUser(
      eventId,
      authUserId
    );

  if (existingRegistration) {

    const error = new Error(
      "Ya estás inscrito en este evento"
    );

    error.statusCode = 409;

    throw error;

  }

  const registration = await createRegistration(
    eventId,
    authUserId
  );

  return registration;
};

const listMyRegistrations = async (authUserId) => {

  return await getMyRegistrations(authUserId);

};

const getRegistrationCounts = async (eventIds) => {

  if (!Array.isArray(eventIds) || eventIds.length === 0) {

    const error = new Error(
      "event_ids debe ser una lista con al menos un evento"
    );

    error.statusCode = 400;

    throw error;

  }

  return await countRegistrationsByEvents(eventIds);

};

const getOrganizerCounts = async (organizerId) => {

  try {

    // Obtener eventos desde microservicio events
    const response = await axios.get(
      "http://localhost:3003/events"
    );

    const events = response.data;

    // Eventos creados por el organizador
    const organizerEvents = events.filter(
    (event) => Number(event.organizer_auth_user_id) === Number(organizerId)
    );

    // Total eventos
    const totalEvents = organizerEvents.length;

    // Eventos activos
    const activeEvents = organizerEvents.filter(
      (event) => event.status === "ACTIVE"
    );

    const activeCount = activeEvents.length;

    // IDs de eventos activos
    const activeIds = activeEvents.map(
      (event) => event.id
    );

    let totalRegistrations = 0;

    // Contar inscritos
    if (activeIds.length > 0) {

      const result = await pool.query(
        `
        SELECT COUNT(*)::int AS total
        FROM event_registrations
        WHERE event_id = ANY($1::bigint[])
          AND registration_status = 'REGISTERED'
        `,
        [activeIds]
      );

      totalRegistrations = parseInt(
        result.rows[0].total
      );

    }

    return {
      total_events: totalEvents,
      active_events: activeCount,
      total_registrations: totalRegistrations,
    };

  } catch (error) {

    console.error(error);

    throw {
      statusCode: 500,
      message: "Error obteniendo estadísticas",
    };

  }

};

const listParticipantsByEvent = async (eventId) => {
  if (!eventId) {
    const error = new Error("event_id es obligatorio");
    error.statusCode = 400;
    throw error;
  }

  return await getParticipantsByEventId(eventId);
};

module.exports = {
  registerToEvent,
  listMyRegistrations,
  getRegistrationCounts,
  getOrganizerCounts,
  listParticipantsByEvent,
};