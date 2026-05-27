const express = require("express");

const verifyToken = require("../middlewares/verifyToken");

const {
  postResult,
  getPublishedEvents,
  getEventResults,
  getEventDraftResults,
  putPublishResults,
} = require("../controllers/resultController");

const router = express.Router();

router.post("/", verifyToken, postResult);

router.get("/events/published", verifyToken, getPublishedEvents);

router.get("/event/:eventId", verifyToken, getEventResults);

router.get("/event/:eventId/drafts", verifyToken, getEventDraftResults);

router.put("/event/:eventId/publish", verifyToken, putPublishResults);

module.exports = router;