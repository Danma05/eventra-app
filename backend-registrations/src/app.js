const express = require("express");
const cors = require("cors");
const registrationRoutes = require("./routes/registrationRoutes");

const app = express();

app.use(cors());
app.use(express.json());

app.use("/registrations", registrationRoutes);

app.get("/", (req, res) => {
  res.json({ message: "Registrations service running" });
});

module.exports = app;