const validateCreateEvent = (req, res, next) => {
  const { title, event_date, location, capacity, image_url } = req.body;

  if (!title || !event_date || !location || capacity === undefined || !image_url) {
    return res.status(400).json({
      message: "title, event_date, location, capacity e image_url son obligatorios",
    });
  }

  if (typeof title !== "string" || title.trim().length < 3) {
    return res.status(400).json({
      message: "title debe tener al menos 3 caracteres",
    });
  }

  if (typeof location !== "string" || location.trim().length < 3) {
    return res.status(400).json({
      message: "location debe tener al menos 3 caracteres",
    });
  }

  if (isNaN(Number(capacity)) || Number(capacity) <= 0) {
    return res.status(400).json({
      message: "capacity debe ser un número mayor a 0",
    });
  }

  if (typeof image_url !== "string" || image_url.trim().length === 0) {
    return res.status(400).json({
      message: "image_url es obligatoria",
    });
  }

  next();
};

const validateUpdateEvent = (req, res, next) => {
  const { capacity, status, title, location } = req.body;

  if (title !== undefined) {
    if (typeof title !== "string" || title.trim().length < 3) {
      return res.status(400).json({
        message: "title debe tener al menos 3 caracteres",
      });
    }
  }

  if (location !== undefined) {
    if (typeof location !== "string" || location.trim().length < 3) {
      return res.status(400).json({
        message: "location debe tener al menos 3 caracteres",
      });
    }
  }

  if (capacity !== undefined) {
    if (isNaN(Number(capacity)) || Number(capacity) <= 0) {
      return res.status(400).json({
        message: "capacity debe ser un número mayor a 0",
      });
    }
  }

  if (status !== undefined) {
    if (!["ACTIVE", "CANCELLED", "FINISHED"].includes(status)) {
      return res.status(400).json({
        message: "status inválido",
      });
    }
  }

  next();
};

module.exports = {
  validateCreateEvent,
  validateUpdateEvent,
};