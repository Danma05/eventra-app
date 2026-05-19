const express = require("express");

const {
  createProfile,
  getMyProfile,
  updateMyProfile,
  getProfileByAuthId,
} = require("../controllers/userController");

const verifyToken = require("../middleware/verifyToken");

const router = express.Router();

router.post("/profile", verifyToken, createProfile);
router.get("/profile/me", verifyToken, getMyProfile);
router.put("/profile/me", verifyToken, updateMyProfile);

router.get("/profile/:authUserId", getProfileByAuthId);

module.exports = router;