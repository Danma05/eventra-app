const {
  createResult,
  getResultsByEvent,
  getDraftResultsByEvent,
  getMyResults,
  publishResultsByEvent,
  deleteResultById,
} = require("../models/resultModel");

const addResult = async (data) => {
  if (!data.event_id || !data.auth_user_id || !data.total_time_seconds) {
    const error = new Error("event_id, auth_user_id y total_time_seconds son obligatorios");
    error.statusCode = 400;
    throw error;
  }

  if (data.total_time_seconds <= 0) {
    const error = new Error("El tiempo total debe ser mayor a 0");
    error.statusCode = 400;
    throw error;
  }

  return await createResult(data);
};

const listPublishedResultsByEvent = async (eventId) => {
  if (!eventId) {
    const error = new Error("eventId es obligatorio");
    error.statusCode = 400;
    throw error;
  }

  return await getResultsByEvent(eventId);
};

const listDraftResultsByEvent = async (eventId) => {
  if (!eventId) {
    const error = new Error("eventId es obligatorio");
    error.statusCode = 400;
    throw error;
  }

  return await getDraftResultsByEvent(eventId);
};

const listMyResults = async (authUserId) => {
  return await getMyResults(authUserId);
};

const publishEventResults = async (eventId) => {
  const draftResults = await getDraftResultsByEvent(eventId);

  if (draftResults.length === 0) {
    const error = new Error("No hay resultados para publicar");
    error.statusCode = 400;
    throw error;
  }

  return await publishResultsByEvent(eventId);
};

const removeResult = async (resultId) => {
  const deleted = await deleteResultById(resultId);

  if (!deleted) {
    const error = new Error("Resultado no encontrado");
    error.statusCode = 404;
    throw error;
  }

  return deleted;
};

module.exports = {
  addResult,
  listPublishedResultsByEvent,
  listDraftResultsByEvent,
  listMyResults,
  publishEventResults,
  removeResult,
};