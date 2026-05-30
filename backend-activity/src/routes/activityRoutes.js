const express = require("express");

const verifyToken = require("../middlewares/verifyToken");

const {
  postStartActivity,
  postActivityLocation,
  postFinishActivity,
  getActiveParticipants,
} = require("../controllers/activityController");

const router = express.Router();

router.post("/start", verifyToken, postStartActivity);

router.post("/location", verifyToken, postActivityLocation);

router.post("/finish", verifyToken, postFinishActivity);

router.get("/event/:eventId/active", verifyToken, getActiveParticipants);

module.exports = router;