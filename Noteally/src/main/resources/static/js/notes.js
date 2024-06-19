//MENU

const indicator = document.querySelector('.nav-indicator');
const items = document.querySelectorAll('.nav-item');

function handleIndicator(el) {
  items.forEach(item => {
    item.classList.remove('is-active');
    item.removeAttribute('style');
  });
  
  indicator.style.width = `${el.offsetWidth}px`;
  indicator.style.left = `${el.offsetLeft}px`;
  indicator.style.backgroundColor = el.getAttribute('active-color');

  el.classList.add('is-active');
  el.style.color = el.getAttribute('active-color');
}


items.forEach((item, index) => {
  item.addEventListener('click', (e) => { handleIndicator(e.target)});
  item.classList.contains('is-active') && handleIndicator(item);
});

const testimonials = document.querySelectorAll('.testimonial');

const boxShadowClasses = [
  'box-shadow1',
  'box-shadow2',
  'box-shadow3',
  'box-shadow4',
  'box-shadow5',
  'box-shadow6',
  'box-shadow7',
  'box-shadow8',
  'box-shadow9',
  'box-shadow10',
  'box-shadow11',
  'box-shadow12',
  'box-shadow13',
  'box-shadow14',
  'box-shadow15',
  'box-shadow16',
  'box-shadow17',
  'box-shadow18',
  'box-shadow19',
  'box-shadow20',
  'box-shadow21',
  'box-shadow22',
  'box-shadow23',
  'box-shadow24',
  'box-shadow25',
  'box-shadow26',
  'box-shadow27',
  'box-shadow28',
  'box-shadow29',
  'box-shadow30',
  'box-shadow31',
  'box-shadow32',
  'box-shadow33',
  'box-shadow34',
  'box-shadow35',
  'box-shadow36',
  'box-shadow37',
  'box-shadow38',
  'box-shadow39',
  'box-shadow40',
  'box-shadow41',
  'box-shadow42',
  'box-shadow43',
  'box-shadow44',
  'box-shadow45',
  'box-shadow46',
  'box-shadow47',
  'box-shadow48',
  'box-shadow49',
  'box-shadow50',
  'box-shadow51',
  'box-shadow52',
];

testimonials.forEach((testimonial) => {
  const randomIndex = Math.floor(Math.random() * boxShadowClasses.length);

  const randomBoxShadowClass = boxShadowClasses[randomIndex];

  testimonial.classList.add(randomBoxShadowClass);
});

