export default function AllInOneSoftware() {
  const features = [
    {
      icon: "📄",
      title: "Online Billing, Invoicing, & Contracts",
      text: "Simple and secure control of your organization’s financial and legal transactions. Send customized invoices and contracts.",
    },
    {
      icon: "📅",
      title: "Easy Scheduling & Attendance Tracking",
      text: "Schedule and reserve classrooms at one or multiple campuses. Keep detailed records of student attendance.",
    },
    {
      icon: "👥",
      title: "Customer Tracking",
      text: "Automate and track emails to individuals or groups. Built-in system helps organize your organization efficiently.",
    },
  ];

  return (
    <section className="bg-gray-900 text-center text-white py-20 px-6 md:px-16">
      <h2 className="text-3xl font-bold mb-4">
        All-In-One <span className="text-[#00b6b6]">Cloud Software.</span>
      </h2>
      <p className="max-w-3xl mx-auto text-gray-300 mb-12">
        TOTC is one powerful online software suite that combines all the tools needed to run a successful school or office.
      </p>

      <div className="grid md:grid-cols-3 gap-8 max-w-6xl mx-auto">
        {features.map((item, idx) => (
          <div
            key={idx}
            className="bg-white text-gray-800 rounded-2xl shadow-lg p-8 relative"
          >
            <div className="absolute -top-6 left-1/2 -translate-x-1/2 bg-gradient-to-r from-[#00b6b6] to-[#0077ff] text-white w-12 h-12 flex items-center justify-center rounded-full shadow-lg text-2xl">
              {item.icon}
            </div>
            <h3 className="mt-8 mb-3 font-bold text-lg">{item.title}</h3>
            <p className="text-gray-600 text-sm leading-relaxed">{item.text}</p>
          </div>
        ))}
      </div>
    </section>
  );
}
