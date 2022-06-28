import UserContext from "../../Helpers/Context";
import { Marker, Popup } from "react-leaflet";
import { useMapEvents } from "react-leaflet";
import React, { useContext } from "react";

import L from "leaflet";
import icon from "leaflet/dist/images/marker-icon.png";
import iconShadow from "leaflet/dist/images/marker-shadow.png";

const AddMarker = () => {
  const { marker, setMarker, draw } = useContext(UserContext);

  let DefaultIcon = L.icon({
    iconUrl: icon,
    shadowUrl: iconShadow,
    iconSize: [24, 40],
    iconAnchor: [12, 40],
  });

  L.Marker.prototype.options.icon = DefaultIcon;

  const map = useMapEvents({
    click(e) {
      console.log(e);
      if (draw.marker) {
        setMarker([...marker, e.latlng]);
      }
    },
  });

  return (
    <>
      {marker.length >= 1 &&
        marker.map((marker, i) => (
          <Marker key={`marker-${i}`} position={marker}>
            <Popup>
              <span>Marker # {i + 1}</span>
            </Popup>
          </Marker>
        ))}
    </>
  );
};

export default AddMarker;
