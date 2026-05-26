const {
  addResult,
  listPublishedEvents,
  listPublishedResultsByEvent,
  listDraftResultsByEvent,
  publishEventResults,
} = require("../services/resultService");

const postResult = async (req, res) => {
  try {
    const result = await addResult(req.body);

    return res.status(201).json({
      message: "Resultado creado en borrador correctamente",
      result,
    });
  } catch (error) {
    return res.status(error.statusCode || 500).json({
      message: error.message,
    });
  }
};

const getPublishedEvents = async (req, res) => {
  try {
    const events = await listPublishedEvents();

    return res.status(200).json(events);
  } catch (error) {
    return res.status(error.statusCode || 500).json({
      message: error.message,
    });
  }
};

const getEventResults = async (req, res) => {
  try {
    const { eventId } = req.params;
    const { search } = req.query;

    const results = await listPublishedResultsByEvent(eventId, search);

    return res.status(200).json(results);
  } catch (error) {
    return res.status(error.statusCode || 500).json({
      message: error.message,
    });
  }
};

const getEventDraftResults = async (req, res) => {
  try {
    const { eventId } = req.params;

    const results = await listDraftResultsByEvent(eventId);

    return res.status(200).json(results);
  } catch (error) {
    return res.status(error.statusCode || 500).json({
      message: error.message,
    });
  }
};

const putPublishResults = async (req, res) => {
  try {
    const { eventId } = req.params;

    const published = await publishEventResults(eventId);

    return res.status(200).json({
      message: "Resultados publicados correctamente",
      published,
    });
  } catch (error) {
    return res.status(error.statusCode || 500).json({
      message: error.message,
    });
  }
};

module.exports = {
  postResult,
  getPublishedEvents,
  getEventResults,
  getEventDraftResults,
  putPublishResults,
};