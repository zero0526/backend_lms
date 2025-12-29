import { ArrowRight } from "lucide-react";

const categories = [
  {
    name: "Lorem Ipsum",
    icon: "🎓",
    courses: [
      { title: "Integer id Orc Sed Ante Tincidunt", price: "$450", img: "https://picsum.photos/200/150?1" },
      { title: "Vestibulum facilisu", price: "$320", img: "https://picsum.photos/200/150?2" },
      { title: "Cras convallis", price: "$270", img: "https://picsum.photos/200/150?3" },
      { title: "Ur sed eros", price: "$499", img: "https://picsum.photos/200/150?4" },
    ],
  },
  {
    name: "Quisque a Consequat",
    icon: "🌕",
    courses: [
      { title: "Integer id Orc Sed Ante Tincidunt", price: "$450", img: "https://picsum.photos/200/150?5" },
      { title: "Ur sed eros", price: "$350", img: "https://picsum.photos/200/150?6" },
      { title: "Cras convallis", price: "$400", img: "https://picsum.photos/200/150?7" },
    ],
  },
  {
    name: "Aenean Facilisis",
    icon: "📷",
    courses: [
      { title: "Integer id Orc Sed Ante Tincidunt", price: "$450", img: "https://picsum.photos/200/150?8" },
      { title: "Ur sed eros", price: "$340", img: "https://picsum.photos/200/150?9" },
      { title: "Cras convallis", price: "$300", img: "https://picsum.photos/200/150?10" },
    ],
  },
];

export default function ExploreCourses() {
  return (
    <section className="bg-[#E9F4FF] p-4 py-20 px-6 md:px-16">
      <div className="max-w-7xl mx-auto">
        <h2 className="text-3xl font-bold mb-2">Explore Course</h2>
        <p className="text-gray-700 mb-12">Ut sed eros finibus, placerat orci id, dapibus.</p>

        {categories.map((cat, idx) => (
          <div key={idx} className="mb-16">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-xl font-semibold flex items-center gap-2">
                <span>{cat.icon}</span> {cat.name}
              </h3>
              <a href="#" className="flex items-center text-[#00b6b6] font-semibold hover:underline">
                SEE ALL <ArrowRight className="ml-1 w-4 h-4" />
              </a>
            </div>

            <div className="flex gap-4 overflow-x-auto pb-4">
              {cat.courses.map((course, i) => (
                <div
                  key={i}
                  className="min-w-[240px] bg-white shadow-lg rounded-xl overflow-hidden flex-shrink-0 transform hover:scale-105 transition duration-300"
                >
                  <img src={course.img} alt={course.title} className="w-full h-40 object-cover" />
                  <div className="p-4">
                    <h4 className="font-bold text-gray-800 text-sm mb-2">{course.title}</h4>
                    <p className="text-gray-500 text-xs mb-3">Cras convallis lacus orci, magna fringilla vel.</p>
                    <div className="flex items-center justify-between">
                      <span className="text-yellow-500">★★★★★</span>
                      <span className="font-semibold">{course.price}</span>
                    </div>
                    <button className="mt-3 w-full border border-[#00b6b6] text-[#00b6b6] rounded-lg py-1 hover:bg-[#00b6b6] hover:text-white transition">
                      Explore
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}
