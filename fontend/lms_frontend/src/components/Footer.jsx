export default function Footer() {
  return (
    <footer className="bg-[#002B3D] text-white py-10 mt-20">
      <div className="max-w-6xl mx-auto text-center">
        <h2 className="text-xl font-semibold mb-3">TOTC Online Learning</h2>
        <p className="text-gray-400 text-sm mb-6">
          Learn smarter, grow faster — your journey begins here.
        </p>
        <div className="flex justify-center space-x-6">
          <a href="#" className="hover:text-[#00b6b6]">Facebook</a>
          <a href="#" className="hover:text-[#00b6b6]">Twitter</a>
          <a href="#" className="hover:text-[#00b6b6]">LinkedIn</a>
        </div>
        <p className="text-xs text-gray-500 mt-6">© 2025 TOTC. All rights reserved.</p>
      </div>
    </footer>
  );
}
