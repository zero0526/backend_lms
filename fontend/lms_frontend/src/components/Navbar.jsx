export default function Navbar() {
  return (
    <nav className="flex justify-between items-center px-10 py-4 bg-[#00b6b6] text-white fixed top-0 w-full z-50">
      <div className="flex items-center space-x-2">
        <div className="bg-white text-[#00b6b6] font-bold text-lg px-2 py-1 rounded">TOTC</div>
      </div>

      <ul className="hidden md:flex space-x-8 font-medium">
        <li><a href="#home" className="hover:text-yellow-200">Home</a></li>
        <li><a href="#courses" className="hover:text-yellow-200">Courses</a></li>
        <li><a href="#careers" className="hover:text-yellow-200">Careers</a></li>
        <li><a href="#blog" className="hover:text-yellow-200">Blog</a></li>
        <li><a href="#about" className="hover:text-yellow-200">About Us</a></li>
      </ul>

      <div className="flex space-x-4">
        <a href="/login" className="bg-white text-[#00b6b6] px-4 py-2 rounded-lg font-medium hover:bg-gray-100">
          Login
        </a>
        <a href="/register" className="bg-[#00b6b6] border border-white text-white px-4 py-2 rounded-lg font-medium hover:bg-white hover:text-[#00b6b6]">
          Register
        </a>
      </div>
    </nav>
  );
}
