const {
  addResult,
  listPublishedResultsByEvent,
  listDraftResultsByEvent,
  listMyResults,
  publishEventResults,
  removeResult,
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

const getEventResults = async (req, res) => {
  try {
    const { eventId } = req.params;

    const results = await listPublishedResultsByEvent(eventId);

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

const getMyResultList = async (req, res) => {
  try {
    const authUserId = req.user.id;

    const results = await listMyResults(authUserId);

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

const deleteResult = async (req, res) => {
  try {
    const { resultId } = req.params;

    const deleted = await removeResult(resultId);

    return res.status(200).json({
      message: "Resultado eliminado correctamente",
      deleted,
    });
  } catch (error) {
    return res.status(error.statusCode || 500).json({
      message: error.message,
    });
  }
};

module.exports = {
  postResult,
  getEventResults,
  getEventDraftResults,
  getMyResultList,
  putPublishResults,
  deleteResult,
};