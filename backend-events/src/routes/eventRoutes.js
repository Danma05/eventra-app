const express = require("express");
const {
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
} = require("../controllers/eventController");
const verifyToken = require("../middlewares/verifyToken");
const { validateCreateEvent, validateUpdateEvent } = require("../middlewares/validateEvent");

const router = express.Router();

router.get("/organizer/my", verifyToken, getOrganizerEvents);
router.get("/", getEvents);
router.get("/:id", getEvent);

router.post("/", verifyToken, validateCreateEvent, postEvent);
router.put("/:id", verifyToken, validateUpdateEvent, putEvent);
router.delete("/:id", verifyToken, destroyEvent);

// Control de carrera en vivo: solo organizador dueño del evento
router.put("/:id/route", verifyToken, putEventRoute);
router.post("/:id/start", verifyToken, postStartRace);
router.post("/:id/pause", verifyToken, postPauseRace);
router.post("/:id/resume", verifyToken, postResumeRace);
router.post("/:id/finish", verifyToken, postFinishRace);

module.exports = router;
