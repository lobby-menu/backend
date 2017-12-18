const options = {
  bio: {
    host: 'http://localhost:8080'
  }
};

function createTableDiv({ face, table, done }){
  const html = `
    <div class="client ${done ? "active" : ""}">
        <div class="photos">
            <div class="photo">
                <image 
                    src="${face}"
                    title="Client Photo"
                />
            </div>
        </div>
        <div class="table-number">${table}</div>
        <div class="table-status">Durum: ${done ? "Sipariş servis edildi" : "Sipariş bekliyor."}</div>
    </div>
  `;

  const a = document.createElement("a");
  a.href = "/details?table=" + table;
  a.innerHTML = html;
  return a;
}

function addToScene(container){
  return (div) => container.appendChild(div);
}

function groupBy(xs, key) {
  return xs.reduce(function(rv, x) {
    (rv[x[key]] = rv[x[key]] || []).push(x);
    return rv;
  }, {});
}

function getPersonForBioID(id){
  return fetch(options.bio.host + `/person/${id}`)
    .then(res => res.json())
}

function parseTime(dateStr){
  return new Date(dateStr).getTime();
}

function mergePersonAndOrder(person, order){
  const face = person.faces.sort((a, b) => parseTime(b['creation_date']) - parseTime(a['creation_date']))[0];
  return { face: face.accessible_url, table: order.table, done: order.done };
}

function singlifyOrders(orders){
  let grouped = groupBy(orders, 'table');

  return Object.keys(grouped).map(table => {
    return grouped[table].sort((a, b) => b['creation_date'] - a['creation_date'])[0];
  }).sort((a, b) => a['table'] - b['table'])
}

function fetchOrders(){
  return fetch('/order/list')
    .then(res => res.json())
    .then(singlifyOrders);
}

window.addEventListener('DOMContentLoaded', () => {
  const container = document.querySelector("#container");
  const addFunc = addToScene(container);

  fetchOrders().then(orders => {
    Promise.all(
      orders
        .map(order =>
          getPersonForBioID(order['bio_identity']).then(person => mergePersonAndOrder(person, order)).then(createTableDiv)
        )
    ).then(divs => divs.forEach(addFunc))
  });

});
