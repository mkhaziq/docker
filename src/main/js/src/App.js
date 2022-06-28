import React, { useState } from "react";
import { SnackbarProvider } from "notistack";
import UserContext from "./Helpers/Context";
import Header from "./components/mapComponent/Header";
import MapExample from "./components/mapComponent/Map";

import Backdrop from "@mui/material/Backdrop";
import CircularProgress from "@mui/material/CircularProgress";

import "./App.css";

const App = () => {
  const [draw, setDraw] = useState({
    marker: false,
    path: false,
    poly: false,
  });
  const [marker, setMarker] = useState([]);
  const [open, setOpen] = useState(false);
  const [gjson, setGjson] = useState({});
  const [poly_geo_json, setPolyJson] = useState({});
  const handleClose = () => {
    setOpen(false);
  };

  return (
    <SnackbarProvider
      maxSnack={8}
      anchorOrigin={{
        horizontal: "left",
        vertical: "bottom",
      }}
    >
      <UserContext.Provider
        value={{
          draw,
          setDraw,
          marker,
          setMarker,
          open,
          setOpen,
          gjson,
          setGjson,
          poly_geo_json,
          setPolyJson,
        }}
      >
        <Header />
        <MapExample
          style={{ width: "100vw", height: "100vh" }}
          zoom={16}
          center={{ lat: 33.6686, lng: 72.9969 }}
        />
        <Backdrop
          sx={{ color: "#fff", zIndex: (theme) => theme.zIndex.drawer + 1 }}
          open={open}
          onClick={handleClose}
        >
          <CircularProgress color="inherit" />
        </Backdrop>
      </UserContext.Provider>
    </SnackbarProvider>
  );
};

const container = document.getElementById("react");
export default App;
