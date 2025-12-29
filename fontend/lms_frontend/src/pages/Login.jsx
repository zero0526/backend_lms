import { Link } from "react-router-dom";
import { useState } from "react";
import { Eye, EyeOff } from "lucide-react";

export default function Login() {
  const [showPassword, setShowPassword] = useState(false);

  return (
    <div className="min-h-screen flex flex-col justify-center items-center bg-gray-100">
      <div className="bg-white shadow-lg rounded-2xl flex flex-col md:flex-row max-w-5xl w-full overflow-hidden">
        {/* Left Image Section */}
        <div className="md:w-1/2 relative">
          <img
            src="https://images.pexels.com/photos/8613086/pexels-photo-8613086.jpeg"
            alt="Classroom"
            className="h-full w-full object-cover"
          />
          <div className="absolute bottom-5 left-5 text-white">
            <h2 className="text-2xl font-semibold">Learn at learn.i</h2>
            <p className="text-sm opacity-90">Learn faster. Grow smarter. Anytime, anywhere.</p>
          </div>
        </div>

        {/* Right Form Section */}
        <div className="md:w-1/2 p-10 flex flex-col justify-center">
          <div className="flex justify-center gap-3 mb-6">
            <Link
              to="/login"
              className="px-6 py-2 rounded-full bg-teal-500 text-white font-medium"
            >
              Login
            </Link>
            <Link
              to="/register"
              className="px-6 py-2 rounded-full border border-teal-500 text-teal-500 font-medium"
            >
              Register
            </Link>
          </div>

          <h2 className="text-xl font-semibold mb-2 text-center">
            Welcome to learn.i
          </h2>
          <p className="text-gray-500 text-center mb-6">
            Empowering your learning journey with modern online courses.
          </p>

          <form className="space-y-4">
            <div>
              <label className="block mb-1 text-gray-600">User name</label>
              <input
                type="text"
                placeholder="Enter your User name"
                className="w-full border border-gray-300 rounded-full px-4 py-2 focus:outline-none focus:ring-2 focus:ring-teal-400"
              />
            </div>

            <div>
              <label className="block mb-1 text-gray-600">Password</label>
              <div className="relative">
                <input
                  type={showPassword ? "text" : "password"}
                  placeholder="Enter your Password"
                  className="w-full border border-gray-300 rounded-full px-4 py-2 pr-10 focus:outline-none focus:ring-2 focus:ring-teal-400"
                />
                <button
                  type="button"
                  className="absolute right-3 top-2.5 text-gray-500"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
            </div>

            <div className="flex items-center justify-between text-sm text-gray-600">
              <label className="flex items-center gap-2">
                <input type="checkbox" className="accent-teal-500" /> Remember me
              </label>
              <a href="#" className="text-teal-500 hover:underline">
                Forgot Password?
              </a>
            </div>

            <button
              type="submit"
              className="w-full bg-teal-500 text-white py-2 rounded-full font-medium hover:bg-teal-600 transition"
            >
              Login
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
