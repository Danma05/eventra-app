require("dotenv").config();

const app = require("./src/app");

const PORT = process.env.PORT || 3006;

app.listen(PORT, "0.0.0.0", () => {
  console.log(`Activity service running on port ${PORT}`);
});