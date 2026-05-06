const {
  registerToEvent,
  listMyRegistrations,
} = require("../services/registrationService");

const postRegistration = async (req, res) => {
  try {
    const authUserId = req.user.id;
    const { event_id } = req.body;

    const registration = await registerToEvent(event_id, authUserId);

    return res.status(201).json({
      message: "Inscripción realizada correctamente",
      registration,
    });
  } catch (error) {
    return res.status(error.statusCode || 500).json({
      message: error.message,
    });
  }
};

const getMyRegistrationList = async (req, res) => {
  try {
    const authUserId = req.user.id;
    const registrations = await listMyRegistrations(authUserId);

    return res.status(200).json(registrations);
  } catch (error) {
    return res.status(error.statusCode || 500).json({
      message: error.message,
    });
  }
};

module.exports = {
  postRegistration,
  getMyRegistrationList,
};