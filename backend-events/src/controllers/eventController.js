const {
  listEvents,
  findEventById,
  registerEvent,
  editEvent,
  removeEvent,
} = require("../services/eventService");

const getEvents = async (req, res) => {
  try {
    const events = await listEvents();
    return res.status(200).json(events);
  } catch (error) {
    return res.status(500).json({
      message: error.message,
    });
  }
};

const getEvent = async (req, res) => {
  try {
    const { id } = req.params;
    const event = await findEventById(id);

    return res.status(200).json(event);
  } catch (error) {
    return res.status(404).json({
      message: error.message,
    });
  }
};

const postEvent = async (req, res) => {
  try {
    const authUserId = req.user.id;
    const event = await registerEvent(authUserId, req.body);

    return res.status(201).json({
      message: "Evento creado correctamente",
      event,
    });
  } catch (error) {
    return res.status(400).json({
      message: error.message,
    });
  }
};

const putEvent = async (req, res) => {
  try {
    const { id } = req.params;
    const event = await editEvent(id, req.body);

    return res.status(200).json({
      message: "Evento actualizado correctamente",
      event,
    });
  } catch (error) {
    return res.status(400).json({
      message: error.message,
    });
  }
};

const destroyEvent = async (req, res) => {
  try {
    const { id } = req.params;
    const event = await removeEvent(id);

    return res.status(200).json({
      message: "Evento eliminado correctamente",
      event,
    });
  } catch (error) {
    return res.status(404).json({
      message: error.message,
    });
  }
};

module.exports = {
  getEvents,
  getEvent,
  postEvent,
  putEvent,
  destroyEvent,
};