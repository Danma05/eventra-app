const express = require("express");
const {
  postRegistration,
  getMyRegistrationList,
  postRegistrationCounts,
  getOrganizerRegistrationCounts,
  getEventParticipants,
} = require("../controllers/registrationController");
const verifyToken = require("../middlewares/verifyToken");

const router = express.Router();

router.post("/", verifyToken, postRegistration);
router.get("/my", verifyToken, getMyRegistrationList);
router.post("/counts", verifyToken, postRegistrationCounts);
router.get("/my/counts", verifyToken, getOrganizerRegistrationCounts);
router.get("/event/:eventId/participants", verifyToken, getEventParticipants);

module.exports = router;