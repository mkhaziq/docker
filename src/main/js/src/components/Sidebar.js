import * as React from "react";
import Drawer from "@mui/material/Drawer";

export default function TemporaryDrawer({ anchor, open, onClose, data }) {
  return (
    <div>
      <Drawer anchor={anchor} open={open} onClose={onClose}>
        {data}
      </Drawer>
    </div>
  );
}
