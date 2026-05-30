const axios = require("axios");

const REGISTRATIONS_SERVICE_URL =
  process.env.REGISTRATIONS_SERVICE_URL || "http://localhost:3004";

const checkUserRegistration = async (eventId, token) => {
  try {
    const response = await axios.get(
      `${REGISTRATIONS_SERVICE_URL}/registrations/check`,
      {
        params: {
          event_id: eventId,
        },
        headers: {
          Authorization: token,
        },
      }
    );

    return response.data;
  } catch (error) {
    return {
      registered: false,
    };
  }
};

module.exports = {
  checkUserRegistration,
};