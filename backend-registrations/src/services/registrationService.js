const {
  findRegistrationByEventAndUser,
  createRegistration,
  getMyRegistrations,
} = require("../models/registrationModel");

const registerToEvent = async (eventId, authUserId) => {
  if (!eventId) {
    const error = new Error("event_id es obligatorio");
    error.statusCode = 400;
    throw error;
  }

  const existingRegistration = await findRegistrationByEventAndUser(eventId, authUserId);

  if (existingRegistration) {
    const error = new Error("Ya estás inscrito en este evento");
    error.statusCode = 409;
    throw error;
  }

  const registration = await createRegistration(eventId, authUserId);
  return registration;
};

const listMyRegistrations = async (authUserId) => {
  return await getMyRegistrations(authUserId);
};

module.exports = {
  registerToEvent,
  listMyRegistrations,
};