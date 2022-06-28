import React, { useContext } from "react";
import UserContext from "../../Helpers/Context";
import { useSnackbar } from "notistack";

// importing material UI components
import {
  Toolbar,
  Typography,
  IconButton,
  Tooltip,
  AppBar,
  TextField,
  MenuItem,
} from "@mui/material/";
import ClearIcon from "@mui/icons-material/Clear";
import AddLocationIcon from "@mui/icons-material/AddLocation";
import LocationOffIcon from "@mui/icons-material/LocationOff";
import MenuIcon from "@mui/icons-material/Menu";
import PolylineIcon from "@mui/icons-material/Polyline";
import RectangleIcon from "@mui/icons-material/Rectangle";
import axios from "axios";

import Sidebar from "../Sidebar";
import { dataApi, success, fail, data } from "../Services";

function Header() {
  const { enqueueSnackbar } = useSnackbar();
  const [state, setState] = React.useState(false);

  const toggleDrawer = () => {
    setState(!state);
  };

  const {
    draw,
    setDraw,
    marker,
    setMarker,
    setOpen,
    gjson,
    setGjson,
    poly_geo_json,
    setPolyJson,
  } = useContext(UserContext);

  const searchPath = () => {
    setOpen(true);
    data.points = marker;
    dataApi("generate")
      .then((res) => {
        success(enqueueSnackbar);
        setGjson(res.data);
      })
      .catch((error) => {
        fail(error, enqueueSnackbar);
      })
      .finally(() => {
        setOpen(false);
      });
  };

  const searchIso = () => {
    setOpen(true);
    data.points = marker;
    dataApi("isochrone")
      .then((res) => {
        setPolyJson(res.data);
        success(enqueueSnackbar);
      })
      .catch((error) => {
        fail(error, enqueueSnackbar);
      })
      .finally(() => {
        setOpen(false);
      });
  };

  return (
    <AppBar position="static">
      <Toolbar>
        {/* <IconButton
          size="large"
          edge="start"
          color="inherit"
          aria-label="menu"
          sx={{ mr: 2 }}
          // onClick={toggleDrawer}
        >
          <MenuIcon />
        </IconButton>
        <Sidebar
          anchor={"left"}
          open={state}
          onClose={toggleDrawer}
          data={
            marker.length >= 1 &&
            marker.map((marker, i) => <span>{marker.lng}</span>)
          }
        /> */}
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
          Main Map
        </Typography>

        <TextField
          variant="outlined"
          select
          label="Choose Option"
          name="option"
        >
          <MenuItem
            onClick={() => {
              setDraw({
                ...draw,
                path: !draw.path,
                marker: !draw.marker,
              });
              if (marker.length >= 2 && draw.marker && draw.path) {
                searchPath();
              }
            }}
          >
            Draw
          </MenuItem>
        </TextField>

        <Tooltip title="Enable/Disable Draw" placement="left-start">
          <IconButton
            size="large"
            edge="end"
            color="inherit"
            aria-label="menu"
            sx={{ mr: 2 }}
            onClick={() => {
              setDraw({
                ...draw,
                path: !draw.path,
                marker: !draw.marker,
              });
              if (marker.length >= 2 && draw.marker && draw.path) {
                searchPath();
              }
            }}
          >
            {draw.path ? <AddLocationIcon /> : <LocationOffIcon />}
          </IconButton>
        </Tooltip>
        <Tooltip title="Enable/Disable Polygon" placement="left-start">
          <IconButton
            size="large"
            edge="end"
            color="inherit"
            aria-label="menu"
            sx={{ mr: 2 }}
            onClick={() => {
              setDraw({
                ...draw,
                poly: !draw.poly,
                marker: !draw.marker,
              });
              if (marker.length >= 1 && draw.marker && draw.poly) {
                searchIso();
              }
            }}
          >
            {draw.poly ? <PolylineIcon /> : <RectangleIcon />}
          </IconButton>
        </Tooltip>
        <Tooltip title="Clear Markers">
          <IconButton
            size="large"
            edge="end"
            color="inherit"
            aria-label="menu"
            sx={{ mr: 2 }}
            onClick={() => {
              setMarker([]);
              setGjson({});
              setPolyJson({});
            }}
          >
            <ClearIcon />
          </IconButton>
        </Tooltip>
      </Toolbar>
    </AppBar>
  );
}

export default Header;
