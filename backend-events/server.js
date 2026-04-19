require("dotenv").config();

const app = require("./src/app");

const PORT = process.env.PORT || 3003;

app.listen(PORT, "0.0.0.0", () => {
  console.log(`Events service running on port ${PORT}`);
});