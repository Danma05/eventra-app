const express = require("express");
const {
  postRegistration,
  getMyRegistrationList,
} = require("../controllers/registrationController");
const verifyToken = require("../middlewares/verifyToken");

const router = express.Router();

router.post("/", verifyToken, postRegistration);
router.get("/my", verifyToken, getMyRegistrationList);

module.exports = router;