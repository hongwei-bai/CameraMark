package cameranativetest.aibdp.jd.com.cameranativetest.view

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cameranativetest.aibdp.jd.com.cameranativetest.R
import kotlinx.android.synthetic.main.fragment_screen.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ScreenFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_screen, container, false)

        return root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dimenTest: Float = resources.getDimension(R.dimen.dimen_test)

        var dimenTestString: String = "unknown"
        when (dimenTest) {
            110f -> dimenTestString = "values"
            111f -> dimenTestString = "values-mdpi"
            213f -> dimenTestString = "values-213dpi"
            112f -> dimenTestString = "values-hdpi"
            113f -> dimenTestString = "values-xhdpi"
            114f -> dimenTestString = "values-xxhdpi"
            115f -> dimenTestString = "values-mdpi"
            116f -> dimenTestString = "values-xxxhdpi_1440x2560"
            117f -> dimenTestString = "values-xxxhdpi"
            else -> "unknown"
        }

        textViewInfo.text = dimenTestString
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
