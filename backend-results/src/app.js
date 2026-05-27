const express = require("express");
const cors = require("cors");

const resultRoutes = require("./routes/resultRoutes");

const app = express();

app.use(cors());
app.use(express.json());

app.use("/results", resultRoutes);

module.exports = app;