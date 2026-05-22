require("dotenv").config();

const app = require("./src/app");

const PORT = process.env.PORT || 3005;

app.listen(PORT, "0.0.0.0", () => {
  console.log(`Results service running on port ${PORT}`);
});