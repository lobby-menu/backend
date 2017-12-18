const options = {
  bio: {
    host: 'http://localhost:8080'
  }
};

function getPersonForBioID(id){
  return fetch(options.bio.host + `/person/${id}`)
    .then(res => res.json())
}

function parseTime(dateStr){
  return new Date(dateStr).getTime();
}

function fetchOrders(){
  return fetch('/order/list')
    .then(res => res.json())
    .then(singlifyOrders);
}


