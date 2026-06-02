const express = require("express");
const verifyToken = require("../middlewares/verifyToken");
const {
  postStartActivity,
  postActivityLocation,
  postFinishActivity,
  postPauseActivity,
  postResumeActivity,
  postFinishEventSessions,
  getLiveRanking,
} = require("../controllers/activityController");

const router = express.Router();

router.post("/start", verifyToken, postStartActivity);
router.post("/location", verifyToken, postActivityLocation);
router.post("/finish", verifyToken, postFinishActivity);
router.post("/pause", verifyToken, postPauseActivity);
router.post("/resume", verifyToken, postResumeActivity);

router.get("/event/:eventId/active", verifyToken, getLiveRanking);
router.get("/event/:eventId/live-ranking", verifyToken, getLiveRanking);
router.post("/event/:eventId/finish-open-sessions", verifyToken, postFinishEventSessions);

module.exports = router;
