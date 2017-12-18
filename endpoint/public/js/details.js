const options = {
  bio: {
    host: 'http://localhost:8080'
  }
};

function getParameterByName(name, url) {
  if (!url) url = window.location.href;
  name = name.replace(/[\[\]]/g, "\\$&");
  var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
    results = regex.exec(url);
  if (!results) return null;
  if (!results[2]) return '';
  return decodeURIComponent(results[2].replace(/\+/g, " "));
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

function fetchOrders(table, done=false, bio=null){
  return fetch(`/order/list?table=${table}&done=${done}${bio != null ? "&bio=" + bio : ""}`)
    .then(res => res.json())
}

function fetchProducts(){
  return fetch('/product/list')
    .then(res => res.json())
}

function createLeftRow({ name, count, price }, order){
  const html = `
      <td>${order + 1}</td>
      <td>${name}</td>
      <td>${count}</td>
      <td>${price}</td>
      <td>
          <i class="fa fa-check" aria-hidden="true"></i>
          <i class="fa fa-times" aria-hidden="true"></i>
      </td>
  `;
  const tr = document.createElement("tr")
  tr.innerHTML = html;
  return tr;
}

function groupByAndMergeItems(products, orders){
  const counts = {};

  orders.forEach(order => {
    order.orders.forEach(single => {
      if(!counts[single.item]) counts[single.item] = 0;
      counts[single.item] += single.count;
    });
  });

  return Object.keys(counts).map(item => {
    const data = products.find(x => x.id === item);
    return { count: counts[item], name: data.name, category: data.categories[0], price: data.price * counts[item] };
  });
}

function shuffle(array) {
  var currentIndex = array.length, temporaryValue, randomIndex;

  // While there remain elements to shuffle...
  while (0 !== currentIndex) {

    // Pick a remaining element...
    randomIndex = Math.floor(Math.random() * currentIndex);
    currentIndex -= 1;

    // And swap it with the current element.
    temporaryValue = array[currentIndex];
    array[currentIndex] = array[randomIndex];
    array[randomIndex] = temporaryValue;
  }

  return array;
}


function createAvatar(idx, mergedOrders, faces){
  const time = new Date(faces[0].creation_date).toLocaleString();
  shuffle(faces);
  const photoSources = faces.slice(0, 4).map(face => `<image src="${face.accessible_url}" title="Client Photo" />`).join("\n");
  const items = mergedOrders.map(({ count, name }) => `<tr><td>${name}</td><td>${count}</td></tr>`).join("\n");

  const html = `
        <div style="float: right">
            <div class="photos">
                ${photoSources}
            </div>
            <div class="name">Kişi ${idx}</div>
        </div>
        <div class="info">
            <span>İlk görülme: ${time}</span>
        </div>
        <div class="history">
            <span>Sipariş Geçmişi</span>
            <table>
                    <tr>
                        <th>Ürün Adı</th>
                        <th>Adet</th>
                    </tr>
                    ${items}
                </table>
        </div>
  `;

  const div = document.createElement('div');
  div.classList = ["person"];
  div.innerHTML = html;
  return div;
}

function parseTime(dateStr){
  return new Date(dateStr).getTime();
}

function init(){
  const table = parseInt(getParameterByName('table'));
  const tableElement = document.querySelector('#order_table');
  const clientElement = document.querySelector('.clients');

  Promise.all([
    fetchOrders(table),
    fetchProducts()
  ]).then(([ orders, products ]) => {
    const mergedItems = groupByAndMergeItems(products, orders);
    mergedItems.map(createLeftRow).forEach(elem => tableElement.append(elem));

    const people = orders.reduce((arr, curr) => {
      const item = curr['bio_identity'];
      if(arr.indexOf(item)) arr.push(item);
      return arr;
    }, []);

    Promise.all(
      people.map(person => Promise.all([fetchOrders(table, true, person), getPersonForBioID(person)]))
    ).then(personsData => {
      personsData.forEach(([ orders, faceData ], idx) => {
        console.log(orders);
        const avatar = createAvatar(
          idx + 1,
          groupByAndMergeItems(products, orders),
          faceData.faces.sort((a, b) => parseTime(a['creation_date']) - parseTime(b['creation_date']))
        );

        clientElement.appendChild(avatar);
      })
    })
  })

}

window.addEventListener('DOMContentLoaded', init);


