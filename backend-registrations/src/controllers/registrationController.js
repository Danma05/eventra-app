const {
  registerToEvent,
  listMyRegistrations,
  getRegistrationCounts,
  getOrganizerCounts,
  listParticipantsByEvent,
} = require("../services/registrationService");

const postRegistration = async (req, res) => {
  try {

    const authUserId = req.user.id;
    const { event_id } = req.body;

    const registration = await registerToEvent(
      event_id,
      authUserId
    );

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

    const registrations = await listMyRegistrations(
      authUserId
    );

    return res.status(200).json(registrations);

  } catch (error) {

    return res.status(error.statusCode || 500).json({
      message: error.message,
    });

  }
};

const postRegistrationCounts = async (req, res) => {
  try {

    const { event_ids } = req.body;

    const counts = await getRegistrationCounts(
      event_ids
    );

    return res.status(200).json(counts);

  } catch (error) {

    return res.status(error.statusCode || 500).json({
      message: error.message,
    });

  }
};

const getOrganizerRegistrationCounts = async (req, res) => {
  try {

    const organizerId = req.user.id;

    const counts = await getOrganizerCounts(
      organizerId
    );

    return res.status(200).json(counts);

  } catch (error) {

    return res.status(error.statusCode || 500).json({
      message: "Error obteniendo estadísticas del organizador",
    });

  }
};

const getEventParticipants = async (req, res) => {
  try {
    const { eventId } = req.params;

    const participants = await listParticipantsByEvent(eventId);

    return res.status(200).json(participants);
  } catch (error) {
    return res.status(error.statusCode || 500).json({
      message: error.message,
    });
  }
};

module.exports = {
  postRegistration,
  getMyRegistrationList,
  postRegistrationCounts,
  getOrganizerRegistrationCounts,
  getEventParticipants,
};