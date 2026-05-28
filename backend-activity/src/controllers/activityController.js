const {
  startActivity,
  registerLocation,
  finishActivity,
  listActiveParticipants,
} = require("../services/activityService");

const postStartActivity = async (req, res) => {
  try {
    const authUserId = req.user.id;
    const { event_id } = req.body;

    const session = await startActivity(event_id, authUserId);

    return res.status(201).json({
      message: "Actividad iniciada correctamente",
      session,
    });
  } catch (error) {
    return res.status(error.statusCode || 500).json({
      message: error.message,
    });
  }
};

const postActivityLocation = async (req, res) => {
  try {
    const authUserId = req.user.id;

    const location = await registerLocation(req.body, authUserId);

    return res.status(201).json({
      message: "Ubicación registrada correctamente",
      location,
    });
  } catch (error) {
    return res.status(error.statusCode || 500).json({
      message: error.message,
    });
  }
};

const postFinishActivity = async (req, res) => {
  try {
    const authUserId = req.user.id;

    const session = await finishActivity(req.body, authUserId);

    return res.status(200).json({
      message: "Actividad finalizada correctamente",
      session,
    });
  } catch (error) {
    return res.status(error.statusCode || 500).json({
      message: error.message,
    });
  }
};

const getActiveParticipants = async (req, res) => {
  try {
    const { eventId } = req.params;

    const participants = await listActiveParticipants(eventId);

    return res.status(200).json(participants);
  } catch (error) {
    return res.status(error.statusCode || 500).json({
      message: error.message,
    });
  }
};

module.exports = {
  postStartActivity,
  postActivityLocation,
  postFinishActivity,
  getActiveParticipants,
};