const axios = require("axios");

const EVENTS_SERVICE_URL = process.env.EVENTS_SERVICE_URL || "http://localhost:3003";

const getEventById = async (eventId) => {
  const response = await axios.get(`${EVENTS_SERVICE_URL}/events/${eventId}`);
  return response.data;
};

module.exports = { getEventById };
