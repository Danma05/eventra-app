const express = require("express");
const cors = require("cors");
const eventRoutes = require("./routes/eventRoutes");

const app = express();

app.use(cors());
app.use(express.json());

app.use("/events", eventRoutes);

app.get("/", (req, res) => {
  res.json({ message: "Events service running" });
});

module.exports = app;