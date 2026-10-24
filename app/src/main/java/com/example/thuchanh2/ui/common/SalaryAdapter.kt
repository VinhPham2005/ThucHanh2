package com.example.thuchanh2.ui.common

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thuchanh2.data.entity.Employee
import com.example.thuchanh2.data.entity.Salary
import com.example.thuchanh2.databinding.ItemSalaryBinding
import java.text.NumberFormat
import java.util.Locale

class SalaryAdapter(
    private var salaryList: List<Salary>,
    private var employeeMap: Map<Int, Employee>,
    private val isAdmin: Boolean,
    private val onToggleStatusClick: ((Salary) -> Unit)? = null
) : RecyclerView.Adapter<SalaryAdapter.SalaryViewHolder>() {

    inner class SalaryViewHolder(val binding: ItemSalaryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalaryViewHolder {
        val binding = ItemSalaryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SalaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SalaryViewHolder, position: Int) {
        val salary = salaryList[position]
        val emp = employeeMap[salary.employeeId]
        val currencyFormat = NumberFormat.getNumberInstance(Locale.US)

        with(holder.binding) {
            tvEmployeeName.text = if (emp != null) {
                "${emp.code} - ${emp.fullname}"
            } else {
                "Nhân viên ID #${salary.employeeId}"
            }

            tvMonthYear.text = String.format(Locale.getDefault(), "Tháng %02d/%d", salary.month, salary.year)

            val rateFormatted = currencyFormat.format(emp?.hourlyRate ?: 0.0)
            tvHourlyRate.text = "Lương giờ: $rateFormatted đ/h | Hệ số OT: x1.5"

            val regularHours = (salary.totalHours - salary.overtimeHours).coerceAtLeast(0.0)
            tvTotalHours.text = String.format(Locale.getDefault(), "Tổng giờ: %.2fh (Thường: %.2fh)", salary.totalHours, regularHours)
            tvOvertimeHours.text = String.format(Locale.getDefault(), "Tăng ca (OT): %.2fh", salary.overtimeHours)

            tvTotalSalary.text = "Tổng lương: ${currencyFormat.format(salary.totalSalary)} đ"

            tvStatus.text = salary.status
            if (salary.status == "PAID") {
                tvStatus.setTextColor(Color.parseColor("#2E7D32"))
                tvStatus.setBackgroundColor(Color.parseColor("#E8F5E9"))
            } else {
                tvStatus.setTextColor(Color.parseColor("#EF6C00"))
                tvStatus.setBackgroundColor(Color.parseColor("#FFF3E0"))
            }

            if (isAdmin) {
                btnToggleStatus.visibility = View.VISIBLE
                btnToggleStatus.text = if (salary.status == "PENDING") {
                    "Đánh dấu ĐÃ THANH TOÁN (PAID)"
                } else {
                    "Chuyển về CHỜ THANH TOÁN (PENDING)"
                }
                btnToggleStatus.setOnClickListener {
                    onToggleStatusClick?.invoke(salary)
                }
            } else {
                btnToggleStatus.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = salaryList.size

    fun updateData(newSalaries: List<Salary>, newEmpMap: Map<Int, Employee>) {
        this.salaryList = newSalaries
        this.employeeMap = newEmpMap
        notifyDataSetChanged()
    }
}
