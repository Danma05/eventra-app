const {
  listEvents,
  findEventById,
  registerEvent,
  editEvent,
  removeEvent,
  listEventsByOrganizer,
  defineRoute,
  startRace,
  pauseRace,
  resumeRace,
  finishRace,
} = require("../services/eventService");

const handleError = (res, error) => res.status(error.statusCode || 500).json({ message: error.message });

const getEvents = async (req, res) => {
  try { return res.status(200).json(await listEvents()); } catch (error) { return handleError(res, error); }
};

const getEvent = async (req, res) => {
  try { return res.status(200).json(await findEventById(req.params.id)); } catch (error) { return handleError(res, error); }
};

const postEvent = async (req, res) => {
  try {
    const event = await registerEvent(req.user.id, req.body);
    return res.status(201).json({ message: "Evento creado correctamente", event });
  } catch (error) { return handleError(res, error); }
};

const putEvent = async (req, res) => {
  try {
    const event = await editEvent(req.params.id, req.body, req.user.id);
    return res.status(200).json({ message: "Evento actualizado correctamente", event });
  } catch (error) { return handleError(res, error); }
};

const destroyEvent = async (req, res) => {
  try {
    const event = await removeEvent(req.params.id, req.user.id);
    return res.status(200).json({ message: "Evento eliminado correctamente", event });
  } catch (error) { return handleError(res, error); }
};

const getOrganizerEvents = async (req, res) => {
  try { return res.status(200).json(await listEventsByOrganizer(req.user.id)); } catch (error) { return handleError(res, error); }
};

const putEventRoute = async (req, res) => {
  try {
    const event = await defineRoute(req.params.id, req.body, req.user.id);
    return res.status(200).json({ message: "Ruta definida correctamente", event });
  } catch (error) { return handleError(res, error); }
};

const postStartRace = async (req, res) => {
  try {
    const event = await startRace(req.params.id, req.user.id);
    return res.status(200).json({ message: "Carrera iniciada por el organizador", event });
  } catch (error) { return handleError(res, error); }
};

const postPauseRace = async (req, res) => {
  try {
    const event = await pauseRace(req.params.id, req.user.id);
    return res.status(200).json({ message: "Carrera pausada por el organizador", event });
  } catch (error) { return handleError(res, error); }
};

const postResumeRace = async (req, res) => {
  try {
    const event = await resumeRace(req.params.id, req.user.id);
    return res.status(200).json({ message: "Carrera reanudada por el organizador", event });
  } catch (error) { return handleError(res, error); }
};

const postFinishRace = async (req, res) => {
  try {
    const event = await finishRace(req.params.id, req.user.id);
    return res.status(200).json({ message: "Carrera finalizada por el organizador", event });
  } catch (error) { return handleError(res, error); }
};

module.exports = {
  getEvents,
  getEvent,
  postEvent,
  putEvent,
  destroyEvent,
  getOrganizerEvents,
  putEventRoute,
  postStartRace,
  postPauseRace,
  postResumeRace,
  postFinishRace,
};
