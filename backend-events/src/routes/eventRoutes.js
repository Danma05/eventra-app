const express = require("express");
const {
  getEvents,
  getEvent,
  postEvent,
  putEvent,
  destroyEvent,
} = require("../controllers/eventController");
const verifyToken = require("../middlewares/verifyToken");

const router = express.Router();

router.get("/", getEvents);
router.get("/:id", getEvent);
router.post("/", verifyToken, postEvent);
router.put("/:id", verifyToken, putEvent);
router.delete("/:id", verifyToken, destroyEvent);

module.exports = router;