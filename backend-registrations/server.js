require("dotenv").config();

const app = require("./src/app");

const PORT = process.env.PORT || 3004;

app.listen(PORT, "0.0.0.0", () => {
  console.log(`Registrations service running on port ${PORT}`);
});