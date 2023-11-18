const cursor = document.querySelector(".cursor")
const cursorP = { x: 0, y: 0 }
const pageP = { x: 0, y: 0 }

const lerp = (start, end, amount = 0.05) => {
	return (1 - amount) * start + amount * end
}

document.addEventListener("mousemove", function (e) {
	cursor.style.opacity = 1
	pageP.x = e.clientX
	pageP.y = e.clientY
})

document.addEventListener("mouseout", function (e) {
	cursor.style.opacity = 0
})

function loop() {
	cursorP.x = lerp(cursorP.x, pageP.x)
	cursorP.y = lerp(cursorP.y, pageP.y)
	cursor.style.transform = `translateX(calc(${cursorP.x}px - 50%)) translateY(calc(${cursorP.y}px - 50%))`
	requestAnimationFrame(loop)
}

loop()
