const axios = require("axios");

const {
  createResult,
  getPublishedEventIds,
  getResultsByEvent,
  getDraftResultsByEvent,
  publishResultsByEvent,
} = require("../models/resultModel");

const EVENTS_SERVICE_URL = process.env.EVENTS_SERVICE_URL;
const USER_SERVICE_URL = process.env.USER_SERVICE_URL;

const addResult = async (data) => {
  if (!data.event_id || !data.auth_user_id || !data.total_time_seconds) {
    const error = new Error("event_id, auth_user_id y total_time_seconds son obligatorios");
    error.statusCode = 400;
    throw error;
  }

  return await createResult(data);
};

const listPublishedEvents = async () => {
  const rows = await getPublishedEventIds();

  const events = [];

  for (const row of rows) {
    try {
      const response = await axios.get(`${EVENTS_SERVICE_URL}/events/${row.event_id}`);
      events.push(response.data);
    } catch {
      events.push({
        id: row.event_id,
        title: `Evento ${row.event_id}`,
        event_date: null,
        distance_km: null,
      });
    }
  }

  return events;
};

const listPublishedResultsByEvent = async (eventId, search) => {
  const results = await getResultsByEvent(eventId);

  const enriched = [];

  for (const result of results) {
    let runnerName = `Corredor #${result.auth_user_id}`;

    try {
      const profileResponse = await axios.get(
        `${USER_SERVICE_URL}/users/profile/${result.auth_user_id}`
      );

      const profile = profileResponse.data;
      runnerName = `${profile.first_name} ${profile.last_name}`;
    } catch {}

    enriched.push({
      ...result,
      runner_name: runnerName,
    });
  }

  if (search && search.trim() !== "") {
    const term = search.toLowerCase();

    return enriched.filter((item) =>
      item.runner_name.toLowerCase().includes(term)
    );
  }

  return enriched;
};

const listDraftResultsByEvent = async (eventId) => {
  return await getDraftResultsByEvent(eventId);
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

module.exports = {
  addResult,
  listPublishedEvents,
  listPublishedResultsByEvent,
  listDraftResultsByEvent,
  publishEventResults,
};