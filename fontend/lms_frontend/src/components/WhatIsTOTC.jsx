export default function WhatIsTOTC() {
  return (
    <section className="bg-gray-900 text-center text-white py-20 px-6 md:px-16">
      <h2 className="text-3xl font-bold mb-4">
        What is <span className="text-[#00b6b6]">TOTC?</span>
      </h2>
      <p className="max-w-3xl mx-auto text-gray-300 mb-12">
        TOTC is a platform that allows educators to create online classes whereby they can store the course materials online;
        manage assignments, quizzes and exams; monitor due dates; grade results and provide students with feedback all in one place.
      </p>

      <div className="grid md:grid-cols-2 gap-10 max-w-5xl mx-auto">
        <div className="relative overflow-hidden rounded-2xl">
          <img
            src="https://images.unsplash.com/photo-1588702547923-7093a6c3ba33?auto=format&fit=crop&w=800&q=80"
            alt="Instructor"
            className="w-full h-72 object-cover opacity-80"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent flex flex-col items-center justify-center">
            <h3 className="text-xl font-semibold mb-2">FOR INSTRUCTORS</h3>
            <button className="border border-white text-white px-6 py-2 rounded-full hover:bg-white hover:text-gray-900 transition">
              Start a class today
            </button>
          </div>
        </div>

        <div className="relative overflow-hidden rounded-2xl">
          <img
            src="https://images.unsplash.com/photo-1522202176988-66273c2fd55f?auto=format&fit=crop&w=800&q=80"
            alt="Students"
            className="w-full h-72 object-cover opacity-80"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent flex flex-col items-center justify-center">
            <h3 className="text-xl font-semibold mb-2">FOR STUDENTS</h3>
            <button className="border border-white text-white px-6 py-2 rounded-full hover:bg-white hover:text-gray-900 transition">
              Enter access code
            </button>
          </div>
        </div>
      </div>
    </section>
  );
}
