import axios from "axios";

export const data = {
  points: [],
  profile: "car",
  hints: {
    map: {},
  },
  headings: [],
  pointHints: [],
  curbsides: [],
  snapPreventions: [],
  pathDetails: [],
  algo: "",
  locale: "en_US",
};

export const success = (enqueueSnackbar) => {
  enqueueSnackbar("Successful", {
    variant: "success",
  });
};

export const fail = (err, enqueueSnackbar) => {
  enqueueSnackbar("Invalid Credentials", {
    variant: "error",
  });
  console.log(err);
};

const beforeAxios = () => {
  data.points = data.points.map((mark) => {
    mark.lon = mark.lng;
    delete mark.lng;
    return mark;
  });
};

const afterAxios = () => {
  data.points = data.points.map((mark) => {
    mark.lng = mark.lon;
    delete mark.lon;
    return mark;
  });
};

export const dataApi = (searchType) =>
  new Promise((resolve, reject) => {
    beforeAxios();
    axios
      .post(`http://localhost:8081/routing/${searchType}/`, data)
      .then((res) => {
        return resolve(res);
      })
      .catch((err) => {
        return reject(err);
      })
      .finally(() => {
        afterAxios();
      });
  });
