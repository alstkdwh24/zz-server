// ------------------ 여행지 카드 ------------------
const travelList = [
    {name: '제주도', img: 'https://via.placeholder.com/208x200'},
    {name: '서울', img: 'https://via.placeholder.com/208x200'},
    {name: '부산', img: 'https://via.placeholder.com/208x200'},
    {name: '강릉', img: 'https://via.placeholder.com/208x200'},
    {name: '인천', img: 'https://via.placeholder.com/208x200'},
    {name: '경주', img: 'https://via.placeholder.com/208x200'},
    {name: '전주', img: 'https://via.placeholder.com/208x200'},
    {name: '광주', img: 'https://via.placeholder.com/208x200'},
    {name: '여수', img: 'https://via.placeholder.com/208x200'},
    {name: '속초', img: 'https://via.placeholder.com/208x200'}
];

const travelContainer = document.getElementById('travelContainer');
travelList.forEach(item => {
    const div = document.createElement('div');
    div.className = 'travel-item';
    div.innerHTML = `<img src="${item.img}" alt="${item.name}"><div>${item.name}</div>`;
    travelContainer.appendChild(div);
});

// 좌우 스크롤
document.getElementById('scrollLeft').addEventListener('click', () => {
    travelContainer.scrollLeft -= travelContainer.offsetWidth * 0.2;
});
document.getElementById('scrollRight').addEventListener('click', () => {
    travelContainer.scrollLeft += travelContainer.offsetWidth * 0.2;
});

// ------------------ 숙소 카드 ------------------
const categories = ["전체", "호텔", "펜션", "리조트", "게스트하우스"];
const hotels = [
    {name:"서울호텔", img:"https://via.placeholder.com/320x180", category:"호텔", location:"서울 강남구", rating:4.8, reviews:1234, price:89000, originalPrice:120000},
    {name:"부산펜션", img:"https://via.placeholder.com/320x180", category:"펜션", location:"부산 해운대구", rating:4.6, reviews:876, price:65000, originalPrice:90000}
];

const categoryButtons = document.getElementById('categoryButtons');
categories.forEach((cat, idx) => {
    const btn = document.createElement('button');
    btn.textContent = cat;
    btn.className = idx===0 ? 'active' : 'inactive';
    categoryButtons.appendChild(btn);
});

const hotelListDiv = document.getElementById('hotelList');
hotels.forEach(hotel => {
    const div = document.createElement('div');
    div.className = 'hotel-card';
    div.innerHTML = `
    <img src="${hotel.img}" alt="${hotel.name}">
    <div class="info">
      <div class="category">${hotel.category}</div>
      <div class="name">${hotel.name}</div>
      <div class="location">${hotel.location}</div>
      <div class="rating"><span>★ ${hotel.rating}</span> <span>${hotel.reviews.toLocaleString()}명 평가</span></div>
      <div>쿠폰 적용시</div>
      <div class="price">${hotel.price.toLocaleString()}원 <span class="original">${hotel.originalPrice.toLocaleString()}원</span></div>
    </div>
  `;
    hotelListDiv.appendChild(div);
});
 // ---------------------------------------------이번주 인기 추천 숙소 카드----------------------------------
const categoriess = ["전체", "호텔", "펜션", "리조트", "게스트하우스"];
const hotelsss = [
    {name:"서울호텔", img:"https://via.placeholder.com/320x180", category:"호텔", location:"서울 강남구", rating:4.8, reviews:1234, price:89000, originalPrice:120000},
    {name:"부산펜션", img:"https://via.placeholder.com/320x180", category:"펜션", location:"부산 해운대구", rating:4.6, reviews:876, price:65000, originalPrice:90000}
];
const categoryButtonss = document.getElementById('categoryButtonss');
categoriess.forEach((cat, idx) => {
    const btn = document.createElement('button');
    btn.textContent = cat;
    btn.className = idx===0 ? 'active' : 'inactive';
    categoryButtonss.appendChild(btn);
});
const hotelListDivs = document.getElementById('hotelLists');
hotelsss.forEach(hotels => {
    const div = document.createElement('div');
    div.className = 'hotel-card';
    div.innerHTML = `
    <img src="${hotels.img}" alt="${hotels.name}">
    <div class="info">
      <div class="category">${hotels.category}</div>
      <div class="name">${hotels.name}</div>
      <div class="location">${hotels.location}</div>
      <div class="rating"><span>★ ${hotels.rating}</span> <span>${hotels.reviews.toLocaleString()}명 평가</span></div>
      <div>쿠폰 적용시</div>
      <div class="price">${hotels.price.toLocaleString()}원 <span class="original">${hotels.originalPrice.toLocaleString()}원</span></div>
    </div>
  `;
    hotelListDivs.appendChild(div);
});