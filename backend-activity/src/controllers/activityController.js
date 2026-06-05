const {
  startActivity,
  registerLocation,
  finishActivity,
  pauseActivity,
  resumeActivity,
  finishOpenSessionsByEvent,
  listLiveRanking,
} = require("../services/activityService");

const handleError = (res, error) => res.status(error.statusCode || 500).json({ message: error.message });

const postStartActivity = async (req, res) => {
  try {
    const session = await startActivity(req.body.event_id, req.user.id, req.headers.authorization);
    return res.status(201).json({ message: "Actividad iniciada correctamente", session });
  } catch (error) { return handleError(res, error); }
};

const postActivityLocation = async (req, res) => {
  try {
    const result = await registerLocation(req.body, req.user.id);
    return res.status(201).json({ message: result.auto_finished ? "Llegaste a la meta. Actividad finalizada automáticamente" : "Ubicación registrada correctamente", ...result });
  } catch (error) { return handleError(res, error); }
};

const postFinishActivity = async (req, res) => {
  try {
    const session = await finishActivity(req.body, req.user.id);
    return res.status(200).json({ message: "Actividad finalizada correctamente", session });
  } catch (error) { return handleError(res, error); }
};

const postPauseActivity = async (req, res) => {
  try {
    const session = await pauseActivity(req.body.activity_session_id, req.user.id);
    return res.status(200).json({ message: "Actividad pausada correctamente", session });
  } catch (error) { return handleError(res, error); }
};

const postResumeActivity = async (req, res) => {
  try {
    const session = await resumeActivity(req.body.activity_session_id, req.user.id);
    return res.status(200).json({ message: "Actividad reanudada correctamente", session });
  } catch (error) { return handleError(res, error); }
};

const postFinishEventSessions = async (req, res) => {
  try {
    const sessions = await finishOpenSessionsByEvent(req.params.eventId);
    return res.status(200).json({ message: "Sesiones abiertas marcadas como DNF", sessions });
  } catch (error) { return handleError(res, error); }
};

const getLiveRanking = async (req, res) => {
  try {
    return res.status(200).json(await listLiveRanking(req.params.eventId));
  } catch (error) { return handleError(res, error); }
};

module.exports = {
  postStartActivity,
  postActivityLocation,
  postFinishActivity,
  postPauseActivity,
  postResumeActivity,
  postFinishEventSessions,
  getActiveParticipants: getLiveRanking,
  getLiveRanking,
};
