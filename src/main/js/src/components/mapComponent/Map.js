import React, { useContext } from "react";
import UserContext from "../../Helpers/Context";
import { MapContainer, TileLayer, GeoJSON } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import AddMarker from "./AddMarker";
import hash from "object-hash";

const MapExample = ({ zoom, center }) => {
  const { gjson, poly_geo_json } = useContext(UserContext);

  return (
    <MapContainer center={center} zoom={zoom} scrollWheelZoom={true}>
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
      {Object.keys(gjson).length !== 0 && (
        <GeoJSON data={gjson} key={hash(gjson)} />
      )}
      {Object.keys(poly_geo_json).length !== 0 && (
        <GeoJSON data={poly_geo_json} key={hash(poly_geo_json)} />
      )}
      <AddMarker />
    </MapContainer>
  );
};

export default MapExample;
