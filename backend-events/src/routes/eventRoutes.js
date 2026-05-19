const express = require("express");

const {
  getEvents,
  getEvent,
  postEvent,
  putEvent,
  destroyEvent,
  getOrganizerEvents,
} = require("../controllers/eventController");

const verifyToken = require("../middlewares/verifyToken");

const {
  validateCreateEvent,
  validateUpdateEvent,
} = require("../middlewares/validateEvent");

const router = express.Router();

router.get("/organizer/my", verifyToken, getOrganizerEvents);
// Públicos
router.get("/", getEvents);
router.get("/:id", getEvent);

// Protegidos
router.post("/", verifyToken, validateCreateEvent, postEvent);
router.put("/:id", verifyToken, validateUpdateEvent, putEvent);
router.delete("/:id", verifyToken, destroyEvent);

module.exports = router;