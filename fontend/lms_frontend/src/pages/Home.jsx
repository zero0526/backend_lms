import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import heroImg from "../assets/hero1.png";
import featureImg from "../assets/features.png";
import ExploreCourses from "../components/ExploreCourses";
import WhatIsTOTC from "../components/WhatIsTOTC";
import AllInOneSoftware from "../components/AllInOneSoftware";

export default function Home() {
  return (
    <div className="bg-white text-gray-800">
      <Navbar />

      {/* Hero Section */}
      <section
  id="home"
  className="flex flex-col md:flex-row items-center justify-between pt-32 px-8 md:px-20 bg-[#00b6b6] text-white relative overflow-hidden"
>
  <div className="max-w-lg space-y-6 z-10">
    <h1 className="text-4xl md:text-5xl font-bold">
      <span className="text-yellow-300">Studying</span> Online is now much easier
    </h1>
    <p className="text-white/80">
      TOTC is an interactive platform that will teach you in a more engaging way.
    </p>
    <div className="flex space-x-4">
      <button className="bg-white text-[#00b6b6] font-semibold px-6 py-3 rounded-lg hover:bg-gray-100">
        Join for Free
      </button>
      <button className="flex items-center space-x-2 border border-white px-6 py-3 rounded-lg hover:bg-white/10">
        <span>▶</span>
        <span>Watch how it works</span>
      </button>
    </div>
  </div>

  {/* Hero Image + Stickers */}
  <div className="relative mt-10 md:mt-0 w-full md:w-1/2">
    <img
      src={heroImg}
      alt="Online learning"
      className="w-full"
    />

    {/* === Sticker 1: 250k Assisted Student === */}
    <div className="absolute top-4 left-4 bg-white text-gray-800 rounded-xl shadow-lg px-4 py-2 flex items-center space-x-2 text-sm">
      <div className="bg-blue-100 text-blue-600 p-2 rounded-lg">
        📅
      </div>
      <div>
        <p className="font-bold">250k</p>
        <p className="text-xs text-gray-500">Assisted Student</p>
      </div>
    </div>

    {/* === Sticker 2: Congratulations === */}
    <div className="absolute top-1/3 right-0 bg-white text-gray-800 rounded-xl shadow-lg px-4 py-3 flex items-center space-x-2 text-sm">
      <div className="bg-orange-100 text-orange-500 p-2 rounded-lg">
        ✉️
      </div>
      <div>
        <p className="font-semibold">Congratulations</p>
        <p className="text-xs text-gray-500">Your admission completed</p>
      </div>
    </div>

    {/* === Sticker 3: User Experience Class === */}
    <div className="absolute bottom-6 left-0 bg-white text-gray-800 rounded-xl shadow-lg px-4 py-3 w-56">
      <div className="flex items-center space-x-2 mb-2">
        <img
          src="https://i.pravatar.cc/40?img=12"
          alt="Instructor"
          className="w-8 h-8 rounded-full"
        />
        <div>
          <p className="font-semibold text-sm">User Experience Class</p>
          <p className="text-xs text-gray-500">Today at 12.00 PM</p>
        </div>
      </div>
      <button className="bg-[#ff005c] text-white text-sm font-semibold px-3 py-1 rounded-lg hover:bg-pink-600">
        Join Now
      </button>
    </div>
  </div>
</section>

{/* ===== SUCCESS SECTION ===== */}
<section className="py-20 text-center bg-white">
  <div className="max-w-5xl mx-auto px-6">
    <h2 className="text-3xl font-bold text-gray-900 mb-4">Our Success</h2>
    <p className="text-gray-600 max-w-2xl mx-auto mb-12">
      Ornare id fames interdum porttitor nulla turpis etiam. Diam vitae sollicitudin at nec nam et mattis gravida.
      Adipiscing a quis ultrices eu ornare tristique vel nisl orci.
    </p>

    <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-5 gap-8">
      {[
        { num: "15K+", label: "Students" },
        { num: "75%", label: "Total success" },
        { num: "35", label: "Main questions" },
        { num: "26", label: "Chief experts" },
        { num: "16", label: "Years of experience" },
      ].map((item, i) => (
        <div key={i}>
          <h3 className="text-4xl font-bold bg-gradient-to-r from-[#00b6b6] to-[#0077ff] bg-clip-text text-transparent">
            {item.num}
          </h3>
          <p className="text-gray-700 mt-2">{item.label}</p>
        </div>
      ))}
    </div>
  </div>
</section>


      {/* All-In-One Cloud Software Section */}
       <AllInOneSoftware />

       {/* What is TOTC Section */}
       <WhatIsTOTC />
      

        {/* ===== FEATURES SECTION ===== */}
      <section className="py-20 bg-gray-50">
        <div className="container mx-auto px-6">
          <h2 className="text-3xl font-bold text-center mb-6">
            Our <span className="text-teal-500">Features</span>
          </h2>
          <p className="text-center text-gray-500 mb-12">
            These extraordinary features make learning activities more efficient.
          </p>
          <img
            src={featureImg}
            alt="Features illustration"
            className="mx-auto rounded-lg shadow-lg"
          />
        </div>
      </section>

      {/*  Explore Courses  */}
      <ExploreCourses />

      {/* News Section */}
      <section className="py-20 text-center">
        <h2 className="text-3xl font-bold mb-8">Latest News and Resources</h2>
        <p className="text-gray-600 mb-10">Stay updated with TOTC’s latest announcements and success stories.</p>
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-10 px-10">
          {[1, 2, 3].map((i) => (
            <div key={i} className="bg-white shadow-lg rounded-xl overflow-hidden">
              <img src={`https://picsum.photos/400/250?random=${i}`} alt="news" />
              <div className="p-6 text-left">
                <span className="text-sm font-semibold text-[#00b6b6]">NEWS</span>
                <h3 className="font-bold text-lg mt-2">TOTC adds new course tools for teachers</h3>
                <p className="text-gray-600 text-sm mt-1">
                  Class has expanded new LMS features for improved online learning experience.
                </p>
              </div>
            </div>
          ))}
        </div>
      </section>

      <Footer />
    </div>
  );
}
