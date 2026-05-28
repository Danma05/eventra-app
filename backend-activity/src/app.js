const express = require("express");
const cors = require("cors");

const activityRoutes = require("./routes/activityRoutes");

const app = express();

app.use(cors());
app.use(express.json());

app.use("/activities", activityRoutes);

module.exports = app;