const express = require("express");

const verifyToken = require("../middlewares/verifyToken");

const {
  postResult,
  getEventResults,
  getEventDraftResults,
  getMyResultList,
  putPublishResults,
  deleteResult,
} = require("../controllers/resultController");

const router = express.Router();

router.post("/", verifyToken, postResult);

router.get("/my", verifyToken, getMyResultList);

router.get("/event/:eventId", verifyToken, getEventResults);

router.get("/event/:eventId/drafts", verifyToken, getEventDraftResults);

router.put("/event/:eventId/publish", verifyToken, putPublishResults);

router.delete("/:resultId", verifyToken, deleteResult);

module.exports = router;